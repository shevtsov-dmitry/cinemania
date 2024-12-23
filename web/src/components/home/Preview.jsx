import { useEffect, useRef, useState } from "react";
import { PosterClass } from "@/src/components/poster/Poster";
import Poster from "@/src/components/poster/Poster";
import Constants from "@/src/constants/Constants";
import { ContentMetadata } from "@/src/types/ContentMetadata";

export default function Preview() {
  const STORAGE_URL = Constants.STORAGE_URL;

  const [posters, setPosters] = useState([]);

  // const [isPostersBlockHovered, setIsPostersBlockHovered] = useState(false)
  const scrollableDivRef = useRef();
  const [postersLoadingMessage, setPostersLoadingMessage] = useState("");

  // *** PREVIEW PANEL
  // const videoPlayerState = useSelector((state) => state.videoPlayer);
  // let isPlayerOpened = videoPlayerState.isPlayerOpened; // ?

  const [isPostersLoaded, setIsPostersLoaded] = useState(false);
  const nonAsciiPattern = /[^\u0000-\u007F]/;
  const postersAmount = 20;

  useEffect(() => {
    async function constructPosters() {
      const url = `${STORAGE_URL}/api/v0/metadata/recent/${postersAmount}`;
      const response = await fetch(url);
      const metadataList = await response.json();

      if (response.status !== 200) {
        console.error(
          `Не удалось получить запрашиваемые метаданные API URL: ${url}`,
        );
        setPostersLoadingMessage(
          "Постерам не удалось загрузиться. Сервер не доступен.",
        );
        return;
      }

      setPosters(collectPosterComponents(metadataList));
    }

    /**
     *
     * @param {[ContentMetadata]} metadataList
     * @returns
     */
    function collectPosterComponents(metadataList) {
      const posterList = new Array(metadataList.length);
      for (const map of metadataList) {
        for (const k in map) {
          if (k === "poster") continue;
          let parsedBase64 = atob(map[k]);
          if (nonAsciiPattern.test(parsedBase64)) {
            parsedBase64 = base64ToUtf8(parsedBase64);
          }
          map[k] = parsedBase64;
        }
        const poster = new PosterClass(map);
        posterList.push(
          <Poster key={poster.metadataId} posterObject={poster} />,
        );
      }
      return posterList;

      /**
       *
       * @param {string} ASCII_parsed_text
       * @returns
       */
      function base64ToUtf8(ASCII_parsed_text) {
        const bytes = new Uint8Array(ASCII_parsed_text.length);
        for (let i = 0; i < ASCII_parsed_text.length; i++) {
          bytes[i] = ASCII_parsed_text.charCodeAt(i);
        }
        return new TextDecoder("utf-8").decode(bytes);
      }
    }

    constructPosters();
  }, []);

  return (
    <div
      id="previews-sequence-block"
      className="flex flex-col justify-center"
      onMouseEnter={() => setIsPostersBlockHovered(true)}
      onMouseLeave={() => setIsPostersBlockHovered(false)}
    >
      <div
        ref={scrollableDivRef}
        className="no-scrollbar relative overflow-x-scroll scroll-smooth p-2"
      >
        <ul className="flex w-fit gap-4">
          {posters.length !== 0 ? (

          ) : (
            <p
              className="text-1xl font-bold text-white opacity-20"
              ref={postersLoadingTextRef}
            >
              Постеры загружаются...
            </p>
          )}
        </ul>
      </div>
      {/* TODO use scrollable div for mobile compatible devices */}
      {/* <SideArrows scrollableDivRef={scrollableDivRef} /> */}
    </div>
  );
}
