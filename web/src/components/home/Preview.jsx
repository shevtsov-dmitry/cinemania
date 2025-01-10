import { useEffect, useRef, useState } from "react";
import { ScrollView, Text, View } from "react-native";
import { PosterClass } from "@/src/components/poster/Poster";
import Poster from "@/src/components/poster/Poster";
import Constants from "@/src/constants/Constants";
import { ContentMetadata } from "@/src/types/ContentMetadata";
import PosterType from "@/src/components/poster/PosterType";
import { parsePathFromExpoGoLink } from "expo-router/build/fork/extractPathFromURL";
import { FlatList } from "react-native";
import PreviewCompilation from "@/src/components/compilations/PreviewCompilation";

/**
 *
 * @returns {JSX.Element}
 */
export default function Preview() {
  const STORAGE_URL = Constants.STORAGE_URL;
  const NON_ASCII_PATTERN = /[^\u0000-\u007F]/;
  const POSTERS_AMOUNT = 20;

  const [postersLoadingMessage, setPostersLoadingMessage] = useState("");
  const [metadataList, setMetadataList] = useState([]);
  const [postersBase64List, setPostersBase64List] = useState([]);

  // const videoPlayerState = useSelector((state) => state.videoPlayer);
  // let isPlayerOpened = videoPlayerState.isPlayerOpened; // ?

  useEffect(() => {
    fetchRecentlyAdded();

    async function fetchRecentlyAdded() {
      fetch(`${STORAGE_URL}/api/v0/metadata/recent/${POSTERS_AMOUNT}`)
        .then((res) => {
          if (res.ok) {
            return res.json();
          } else {
            const errmes = decodeURI(res.headers.get("Message")).replaceAll(
              "+",
              " ",
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
            // res.json().then((data) => {
            //   console.log("love");
            // });
            return res.json();
          } else {
            const errmes = decodeURI(res.headers.get("Message")).replaceAll(
              "+",
              " ",
            );
            console.error(errmes);
            setPostersLoadingMessage(errmes);
          }
        })
        .then((fetchedBase64Array) => setPostersBase64List(fetchedBase64Array));
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
        <PreviewCompilation
          posters={postersBase64List}
          metadataList={metadataList}
          errmes={postersLoadingMessage}
        />
      </View>
    </View>
  );
}
