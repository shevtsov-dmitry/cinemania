import React from "react";
import { Route, Routes } from "react-router-dom";
import { Preview } from "./Main/Preview";

export function FilmPage({ videoId }) {
    console.log("videoId: " + videoId)
    return <div className="w-dvw h-dvh flex justify-center flex-col">
        {/*return <div className="w-dvw h-dvh flex justify-center flex-col bg-pink-400">*/}

        <video className="w-fit h-fit" controls autoPlay={true} muted={true}>
            <p className="w-[5%] select-none text-2xl font-bold hover:cursor-pointer text-white ">
                X
            </p>
            <source src={`${process.env.REACT_APP_SERVER_URL}:8081/videos/stream/${videoId}`} type="video/mp4" />
            Your browser does not support the video tag.
        </video>

        <Routes>
            <Route path="/" element={<Preview isPlayerOpened={false} />} ></Route>
        </Routes>
    </div>
}
