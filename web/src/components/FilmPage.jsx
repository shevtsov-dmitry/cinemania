import React from "react";
import { Route, Routes } from "react-router-dom";
import { Preview } from "./Main/Preview";
import Home from "./Main/Home";
import {useSelector} from "react-redux";
import {videoPlayerSlice} from "../store/videoPlayerSlice";

export function FilmPage() {
    const videoPlayerState = useSelector(state => state.videoPlayer)
    const videoId = videoPlayerState.videoId

    return <div className="w-dvw h-dvh flex justify-center flex-col">
        <video className="w-fit h-fit" controls autoPlay={true} muted={true}>
            <p className="w-[5%] select-none text-2xl font-bold hover:cursor-pointer text-white ">
                X
            </p>
            <source src={`${process.env.REACT_APP_SERVER_URL}:8081/videos/stream/${videoId}`} type="video/mp4" />
            Your browser does not support the video tag.
        </video>

        <Routes>
            <Route path="/" element={<Home />} />
        </Routes>
    </div>
}
