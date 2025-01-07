import {useEffect, useState} from "react";

import ContentMetadata from "@/src/types/ContentMetadata";
import PosterType from "./PosterType";

/**
 *
 * @param {Object} props
 * @param {PosterType} props.posterType - predefined PosterType (enum)
 * @param {ContentMetadata} props.metadata - list of content metadata for each poster fetched from server
 * @param {string} props.base64 - encoded image in basic base 64 jpeg format
 * @returns {JSX.Element}
 */
export default function Poster({
                                   posterType = PosterType.DEFAULT,
                                   metadata,
                                   base64,
                               }) {
    const [isPosterHovered, setIsPosterHovered] = useState(false);

    /**
     *
     * @returns {JSX.Element}
     */
    const ContentInformation = () => (
        <>
            <h3 className="bg-inherit text-3xl font-bold text-white">
                {metadata.title}
            </h3>
            <p className="select-none text-white">{}</p>
            <p className="select-none text-white">{metadata.country}</p>
            <p className="select-none text-white">{metadata.mainGenre}</p>
            <p className="select-none text-white text-xs">{metadata.subGenres}</p>
            <p className="select-none text-white">{metadata.releaseDate}</p>
        </>
    );

    return (
        <div
            className={
                `${posterType.PREVIEW && "h-[200px] w-[150px]"} ` +
                `${posterType.DEFAULT && "h-96 w-64"} ` +
                ` z-10 rounded-3xl bg-indigo-900 bg-cover bg-center transition-all hover:scale-105 hover:cursor-pointer`
            }
            style={{backgroundImage: `url(${`data:image/jpeg;base64,${base64}`})`}}
            onMouseEnter={() => setIsPosterHovered(true)}
            onMouseLeave={() => setIsPosterHovered(false)}
        >
            {/*<div className="postersInfo">*/}
            {/*    {isPosterHovered && (*/}
            {/*        <div>*/}
            {/*            <ContentInformation/>*/}
            {/*            <div className="flex w-64 justify-center">*/}
            {/*                /!* <Link *!/*/}
            {/*                /!*     className="absolute bottom-0 z-10 mb-10" *!/*/}
            {/*                /!*     to={`/watch/${metadata.videoId}`} *!/*/}
            {/*                /!* > *!/*/}
            {/*                <button*/}
            {/*                    className="select-none rounded-3xl bg-pink-700 p-4 font-sans text-2xl font-bold text-white opacity-75 hover:opacity-95"*/}
            {/*                    // onClick={() => {};*/}
            {/*                    //     // TODO rewrite to Zustand*/}
            {/*                    //     // dispatch(setVideoId(metadata.videoId))*/}
            {/*                    // }*/}
            {/*                >*/}
            {/*                    Смотреть*/}
            {/*                </button>*/}
            {/*                /!* </Link> *!/*/}
            {/*            </div>*/}
            {/*        </div>*/}
            {/*    )}*/}
            {/*</div>*/}
        </div>
    );
}
