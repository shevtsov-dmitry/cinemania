// TODO use expo router instead
// import { Link } from 'react-router-dom';
// import { setVideoId } from '../store/videoPlayerSlice';

import Constants from "@/src/constants/Constants";
import { useState } from "react";

export default function VideoPlayer() {
  const STREAMING_SERVER_URL = Constants.STREAMING_SERVER_URL;
  // const [videoname, setVideoname] = useState(null);
  // const videoname = "likeid731893";
  const videoname = "SampleVideo_1280x720_30mb_1.mp4";
  const videoSrc = `${STREAMING_SERVER_URL}/stream/${videoname}`;

  return (
    <View>
      <Video controls width="640" height="360" muted autoPlay>
        {/* <source src={videoSrc} type="video/mp4" /> */}
        Your browser does not support the video tag. <br />
        Ваш браузер не может воспроизвести видео.
      </Video>
    </View>
  );
}
