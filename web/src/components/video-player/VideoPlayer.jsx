// TODO use expo router instead
// import { Link } from 'react-router-dom';
// import { setVideoId } from '../store/videoPlayerSlice';

import { useState } from "react";

export default function VideoPlayer() {
  // const [videoname, setVideoname] = useState(null);
  // setVideoname()
  // const videoname = "likeid731893";
  const videoname = "SampleVideo_1280x720_30mb_1.mp4";
  const videoSrc = "http://localhost:8443/stream/" + videoname;

  return (
    <div>
      <h1 className="text-white text-xl">
        NO ONE TREAT TO READ MY LIES NO ONE BUT YOU WISH YOU WEREN'T TRUE
      </h1>
      <video controls width="640" height="360" muted autoPlay>
        <source src={videoSrc} type="video/mp4" />
        Your browser does not support the video tag. <br />
        Ваш браузер не может воспроизвести видео.
      </video>
    </div>
  );
}
