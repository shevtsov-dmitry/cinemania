package com.netty_http3;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.incubator.codec.http3.*;
import io.netty.incubator.codec.quic.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class NettyHttp3Application {

	public static void main(String[] args) {
		SpringApplication.run(NettyHttp3Application.class, args);
	}

	@Component
	public static class Http3Server {
		private static final byte[] CONTENT = "Hello World!\r\n".getBytes(CharsetUtil.US_ASCII);
		private static final int PORT = 9999;

		@EventListener(ApplicationReadyEvent.class)
		public void startHttp3Server() throws Exception {
			NioEventLoopGroup group = new NioEventLoopGroup(1);
			
			// Generate self-signed certificate
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();

			X500Name dnName = new X500Name("CN=localhost");
			BigInteger certSerialNumber = new BigInteger(Long.toString(System.currentTimeMillis()));
			ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA").build(keyPair.getPrivate());
			JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
					dnName, certSerialNumber, new Date(), new Date(System.currentTimeMillis() + 86400000L),
					dnName, keyPair.getPublic());

			X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certBuilder.build(contentSigner));

			QuicSslContext sslContext = QuicSslContextBuilder.forServer(keyPair.getPrivate(), null, cert)
					.applicationProtocols(Http3.supportedApplicationProtocols()).build();

			ChannelHandler codec = Http3.newQuicServerCodecBuilder()
					.sslContext(sslContext)
					.maxIdleTimeout(5000, TimeUnit.MILLISECONDS)
					.initialMaxData(10000000)
					.initialMaxStreamDataBidirectionalLocal(1000000)
					.initialMaxStreamDataBidirectionalRemote(1000000)
					.initialMaxStreamsBidirectional(100)
					.tokenHandler(InsecureQuicTokenHandler.INSTANCE)
					.handler(new ChannelInitializer<QuicChannel>() {
						@Override
						protected void initChannel(QuicChannel ch) {
							ch.pipeline().addLast(new Http3ServerConnectionHandler(
									new ChannelInitializer<QuicStreamChannel>() {
										@Override
										protected void initChannel(QuicStreamChannel ch) {
											ch.pipeline().addLast(new Http3RequestStreamInboundHandler() {
												@Override
												protected void channelRead(ChannelHandlerContext ctx, Http3HeadersFrame frame) {
													ReferenceCountUtil.release(frame);
												}

												@Override
												protected void channelRead(ChannelHandlerContext ctx, Http3DataFrame frame) {
													ReferenceCountUtil.release(frame);
												}

												@Override
												protected void channelInputClosed(ChannelHandlerContext ctx) {
													Http3HeadersFrame headersFrame = new DefaultHttp3HeadersFrame();
													headersFrame.headers().status("200");
													headersFrame.headers().add("server", "netty");
													headersFrame.headers().addInt("content-length", CONTENT.length);
													ctx.write(headersFrame);
													ctx.writeAndFlush(new DefaultHttp3DataFrame(
																	Unpooled.wrappedBuffer(CONTENT)))
															.addListener(QuicStreamChannel.SHUTDOWN_OUTPUT);
												}
											});
										}
									}));
						}
					}).build();

			try {
				Bootstrap bs = new Bootstrap();
				Channel channel = bs.group(group)
						.channel(NioDatagramChannel.class)
						.handler(codec)
						.bind(new InetSocketAddress(PORT)).sync().channel();
				
				System.out.println("HTTP/3 server started on port " + PORT);
				
				channel.closeFuture().sync();
			} finally {
				group.shutdownGracefully();
			}
		}
	}
}
