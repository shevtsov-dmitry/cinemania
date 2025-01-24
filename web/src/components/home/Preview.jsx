import { PosterClass } from "@/src/components/poster/Poster";
import Poster from "@/src/components/poster/Poster";
import Constants from "@/src/constants/Constants";
import { parsePathFromExpoGoLink } from "expo-router/build/fork/extractPathFromURL";
import { View, FlatList } from "react-native";
import Compilation from "@/src/components/compilations/Compilation";
import { parseSplittedWithDefaultDelimiter } from "@/src/utils/BinaryContentUtils";
import { useState, useEffect } from "react";

/**
 *
 * @returns {JSX.Element}
 */
export default function Preview() {
  const STORAGE_URL = Constants.STORAGE_URL;
  const NON_ASCII_PATTERN = /[^\u0000-\u007F]/;
  const POSTERS_AMOUNT = 20;

  /** @type {[string, function(string): void]}  */
  const [postersLoadingMessage, setPostersLoadingMessage] = useState("");
  /** @type {[ContentMetadata[], function(ContentMetadata[]): void} */
  const [metadataList, setMetadataList] = useState([]);
  /** @type {[string[], function(string[]): void]} */
  const [posterImagesUrls, setPosterImagesUrls] = useState([]);

  // const videoPlayerState = useSelector((state) => state.videoPlayer);
  // let isPlayerOpened = videoPlayerState.isPlayerOpened; // ?

  useEffect(() => {
    fetchRecentlyAddedFilmsMetadata();

    async function fetchRecentlyAddedFilmsMetadata() {
      fetch(`${STORAGE_URL}/api/v0/metadata/recent/${POSTERS_AMOUNT}`)
        .then((res) => {
          if (res.ok) {
            return res.json();
          } else {
            const errmes = decodeURI(res.headers.get("Message")).replaceAll(
              "+",
              " "
            );
            console.error(errmes);
            setPostersLoadingMessage(errmes);
          }
        })
        .then((fetchedMetadata) => setMetadataList(fetchedMetadata));
    }
  }, []);

  useEffect(() => {
    fetchPosters();

    async function fetchPosters() {
      if (metadataList.length === 0) return;
      const joinedIds = metadataList
        .map((item) => item.posterMetadata.id)
        .join(",");
      fetch(`${STORAGE_URL}/api/v0/posters/${joinedIds}`)
        .then((res) => {
          if (res.ok) {
            return res.blob();
          } else {
            const errmes = decodeURI(res.headers.get("Message")).replaceAll(
              "+",
              " "
            );
            console.error(errmes);
            setPostersLoadingMessage(errmes);
          }
        })
        .then((binaryOctetStream) => setPosterImagesUrls(parseSplittedWithDefaultDelimiter(binaryOctetStream)));
    }
  }, [metadataList]);

  /**
   *
   * @param {string} ASCII_parsed_text
   * @returns
   */
  // TODO figure out if i should use it to parse russian text to display it in posters info on hover
  function base64ToUtf8(ASCII_parsed_text) {
    const bytes = new Uint8Array(ASCII_parsed_text.length);
    for (let i = 0; i < ASCII_parsed_text.length; i++) {
      bytes[i] = ASCII_parsed_text.charCodeAt(i);
    }
    return new TextDecoder("utf-8").decode(bytes);
  }

  return (
    <View id="previews-sequence-block" className="flex flex-col justify-center">
      <View className="no-scrollbar relative overflow-x-scroll scroll-smooth p-2">
        <Compilation
          posterImageUrls={posterImagesUrls}
          metadataList={metadataList}
          errmes={postersLoadingMessage}
        />
      </View>
    </View>
  );
}
