import Hls from 'hls.js'
import React, { ReactElement, useEffect, useRef } from 'react'

interface WebVideoPlayerProps {
    url: string
    variant?: 'trailer' | 'main'
    className?: string
}

const WebVideoPlayer = ({
    url,
    variant = 'main',
    className = '',
}: WebVideoPlayerProps): ReactElement => {
    const videoRef = useRef<HTMLVideoElement>(null)

    useEffect(() => {
        const video = videoRef.current
        if (video) {
            if (Hls.isSupported()) {
                const hls = new Hls()
                hls.loadSource(url)
                hls.attachMedia(video)
                hls.on(Hls.Events.MANIFEST_PARSED, () => {
                    video.play()
                })
            } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
                video.src = url
                video.addEventListener('loadedmetadata', () => {
                    video.play()
                })
            }
        }
    }, [url])

    return variant === 'main' ? (
        <video
            ref={videoRef}
            controls
            className={`fixed inset-0 z-50 h-screen w-screen bg-black object-contain ${className} `}
        />
    ) : (
        <video ref={videoRef} className="h-auto w-full" />
    )
}

export default WebVideoPlayer
