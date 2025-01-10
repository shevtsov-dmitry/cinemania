import { useEffect, useState } from "react";
import Poster from "../poster/Poster";
import { View, Text, FlatList } from "react-native";
import { ContentMetadata } from "@/src/types/ContentMetadata";
import PosterType from "../poster/PosterType";

/**
 *
 * @param {[string]} string array of base 64 poster images
 * @param {[ContentMetadata]} metadata list for posters which will be shown when poster is selected
 * @param {string?} errmes optional error message to diplay instead of loaded posters
 * @returns {JSX.Element}
 */
export default function PreviewCompilation({
  posters,
  metadataList,
  errmes = "постеры загружаются...",
}) {
  return (
    <>
      {posters.length !== 0 ? (
        <FlatList
          data={metadataList}
          horizontal
          keyExtractor={(item) => item.id}
          renderItem={({ item, index }) => (
            <Poster
              posterType={PosterType.PREVIEW}
              metadata={item}
              base64={posters[index]}
            />
          )}
          contentContainerStyle={{
            flexDirection: "row",
            gap: 16,
            padding: 12,
            backgroundColor: "white",
          }}
        />
      ) : (
        <Text className={"text-1xl font-bold text-white opacity-20"}>
          {errmes}
        </Text>
      )}
    </>
  );
}
