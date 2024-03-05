import React from "react";

export function FilmPage() {
    return <div className="w-dvw h-dvh  flex justify-center flex-col bg-pink-400">

        <video className="w-fit h-fit" controls>
            <p className="w-[5%] select-none text-2xl font-bold hover:cursor-pointer text-white ">
                X
            </p>
            <source src="http://localhost:8081/videos/stream/65e5f9010986dd76a8edb246" type="video/mp4"/>
            Your browser does not support the video tag.
        </video>
    </div>
}