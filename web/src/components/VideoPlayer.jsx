import React from "react";
import {useDispatch, useSelector} from "react-redux";
import {setPlayerOpened, videoPlayerSlice} from "../store/videoPlayerSlice";

export function VideoPlayer() {
    const dispatch = useDispatch()
    const videoPlayerState = useSelector(state => state.videoPlayer)
    const videoId = videoPlayerState.videoId

    console.log("VIDEO ID: " + videoId)

    return <div className="w-dvw h-dvh flex justify-center flex-col">
        <video className="w-fit h-fit" controls autoPlay={true} muted={true}>
            <p className="w-[5%] select-none text-2xl font-bold hover:cursor-pointer text-white "
               onClick={()=>{
                   dispatch(setPlayerOpened(false))
               }}
            >
                X
            </p>
            <source src={`${process.env.REACT_APP_SERVER_URL}:8081/videos/stream/${videoId}`} type="video/mp4" />
            Your browser does not support the video tag.
        </video>
    </div>
}
