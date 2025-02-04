import ContentDetails from "@/src/types/ContentMetadata";
import React, { ReactElement, useEffect, useRef, useState } from "react";
import {
  Animated,
  FlatList,
  InteractionManager,
  Text,
  TouchableOpacity,
  View,
} from "react-native";
import CompilationKind from "./CompilationKind";
import Poster from "@/src/components/poster/Poster";

interface CompilationProps {
  posterImageUrls: string[];
  metadataList: ContentDetails[];
  errmes?: string;
}

/**
 * @note posterImageUrls temporary image URLs array; each one can be inserted in the `<img src="...">` tag to display an image
 */
const Compilation = ({
  posterImageUrls,
  metadataList,
  errmes = "постеры загружаются...",
}: CompilationProps): ReactElement => {
  const [focusedIndex, setFocusedIndex] = useState<number>(0);
  const [selectedIndex, setSelectedIndex] = useState<number>();

  const scaleAnim = useRef(new Animated.Value(1)).current;

  const handleFocus = (index: number) => {
    Animated.timing(scaleAnim, {
      toValue: 1.1,
      duration: 200,
      useNativeDriver: true,
    }).start();
    setFocusedIndex(index);
  };

  const handleBlur = () => {
    Animated.timing(scaleAnim, {
      toValue: 1,
      duration: 200,
      useNativeDriver: true,
    }).start();
  };

  // TODO manage TV function if have time
  // const handleTVKeyPress = (event) => {
  //   InteractionManager.runAfterInteractions(() => {
  //     if (event.eventKeyAction === 0) {
  //       // Key pressed
  //       if (event.keyCode === 21) {
  //         // Left
  //         setFocusedIndex(
  //           (prevIndex) =>
  //             (prevIndex - 1 + metadataList.length) % metadataList.length,
  //         );
  //       } else if (event.keyCode === 22) {
  //         // Right
  //         setFocusedIndex((prevIndex) => (prevIndex + 1) % metadataList.length);
  //       } else if (event.keyCode === 23) {
  //         // Select
  //         setSelectedIndex(focusedIndex);
  //       }
  //     }
  //   });
  // };

  useEffect(() => {
    const listener = InteractionManager.createInteractionHandle();
    // TODO execute action on tv remote controller button press
    // listener.enable(null, handleTVKeyPress);
    //
    // return () => listener.disable();
  }, [focusedIndex]);

  interface SelectablePosterProps {
    metadata: ContentDetails;
    index: number;
  }

  const SelectablePoster = ({
    metadata,
    index,
  }: SelectablePosterProps): ReactElement => {
    const isFocused = index === focusedIndex;
    const isSelected = index === selectedIndex;

    return (
      <TouchableOpacity
        activeOpacity={1}
        onPress={() => setSelectedIndex(index)}
        onFocus={() => handleFocus(index)}
        onBlur={handleBlur}
      >
        <View className={`${isFocused || (isSelected && "scale-150")}`}>
          <Poster
            compilationKind={CompilationKind.PREVIEW}
            metadata={metadata}
            imageUrl={posterImageUrls[index]}
          />
        </View>
      </TouchableOpacity>
    );
  };

  return (
    <>
      {posterImageUrls.length !== 0 ? (
        <FlatList
          data={metadataList}
          horizontal
          keyExtractor={(item: ContentDetails) => item.id}
          renderItem={SelectablePoster}
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
};

export default Compilation;
