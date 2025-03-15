import React, { ReactElement, useEffect, useRef } from "react";
import Hls from "hls.js";

interface WebVideoPlayerProps {
  url: string;
}

const WebVideoPlayer = ({ url }: WebVideoPlayerProps): ReactElement => {
  const videoRef = useRef<HTMLVideoElement>(null);

  useEffect(() => {
    const video = videoRef.current;
    if (video) {
      if (Hls.isSupported()) {
        const hls = new Hls();
        hls.loadSource(url);
        hls.attachMedia(video);
        hls.on(Hls.Events.MANIFEST_PARSED, () => {
          video.play();
        });
      } else if (video.canPlayType("application/vnd.apple.mpegurl")) {
        // Fallback for Safari
        video.src = url;

        video.addEventListener("loadedmetadata", () => {
          video.play();
        });
      }
    }
  }, []);

  return (
    <video ref={videoRef} controls style={{ width: "100%", height: "auto" }} />
  );
};

export default WebVideoPlayer;
