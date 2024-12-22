import { useState } from "react";
import { ContentMetadata } from "../types/ContentMetadata";

/**
 *
 * @param {ContentMetadata} metadata
 * @returns
 */
export default function Poster({ metadata }) {
  const [isPosterHovered, setIsPosterHovered] = useState(false);

  const ContentInformation = () => {
    return (
      <div className="absolute h-96 w-64 rounded-3xl bg-black p-4 opacity-70 content-['']">
        <h3 className="bg-inherit text-3xl font-bold text-white">
          {metadata.title}
        </h3>
        <p className="select-none text-white">{metadata.age}</p>
        <p className="select-none text-white">{metadata.country}</p>
        <p className="select-none text-white">{metadata.mainGenre}</p>
        <p className="select-none text-xs text-white">{metadata.subGenres}</p>
        <p className="select-none text-white">{metadata.releaseDate}</p>
      </div>
    );
  };

  return (
    <div
      className={
        "z-10 h-96 w-64 rounded-3xl bg-indigo-900 bg-cover bg-center transition-all hover:scale-105 hover:cursor-pointer"
      }
      style={{ backgroundImage: `url(${metadata.poster})` }}
      onMouseEnter={() => setIsPosterHovered(true)}
      onMouseLeave={() => setIsPosterHovered(false)}
    >
      <div className="postersInfo">
        {isPosterHovered && (
          <div>
            <ContentInformation />
            <div className="flex w-64 justify-center">
              {/* <Link */}
              {/*     className="absolute bottom-0 z-10 mb-10" */}
              {/*     to={`/watch/${metadata.videoId}`} */}
              {/* > */}
              <button
                className="select-none rounded-3xl bg-pink-700 p-4 font-sans text-2xl font-bold text-white opacity-75 hover:opacity-95"
                // onClick={() => {};
                //     // TODO rewrite to Zustand
                //     // dispatch(setVideoId(metadata.videoId))
                // }
              >
                Смотреть
              </button>
              {/* </Link> */}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
