import { useEffect, useState, useRef } from "react";
import Poster from "../poster/Poster";
import {
  View,
  Text,
  FlatList,
  TVEventHandler,
  Animated,
  TouchableOpacity,
  InteractionManager,
} from "react-native";
import { ContentMetadata } from "@/src/types/ContentMetadata";
import PosterType from "./PosterType";

/**
 * @param {Object} props
 * @param {string[]} props.posterImageUrls temporary image URLs array; each one can be inserted in the `<img src="...">`  tag to display image
 * @param {ContentMetadata[]} props.metadata list with metadata for each poster (which will be shown when poster is selected)
 * @param {string} props.errmes optional error message to diplay when posters are not loaded
 * @returns {JSX.Element}
 */
export default function Compilation({
  posterImageUrls,
  metadataList,
  errmes = "постеры загружаются...",
}) {
  const [focusedIndex, setFocusedIndex] = useState(0);
  const [selectedIndex, setSelectedIndex] = useState(null);
  const scaleAnim = useRef(new Animated.Value(1)).current;

  const handleFocus = (index) => {
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

  const handleTVKeyPress = (event) => {
    InteractionManager.runAfterInteractions(() => {
      if (event.eventKeyAction === 0) {
        // Key pressed
        if (event.keyCode === 21) {
          // Left
          setFocusedIndex(
            (prevIndex) =>
              (prevIndex - 1 + metadataList.length) % metadataList.length,
          );
        } else if (event.keyCode === 22) {
          // Right
          setFocusedIndex((prevIndex) => (prevIndex + 1) % metadataList.length);
        } else if (event.keyCode === 23) {
          // Select
          setSelectedIndex(focusedIndex);
        }
      }
    });
  };

  useEffect(() => {
    const listener = InteractionManager.createInteractionHandle();
    // TODO execute action on tv remote controller button press
    // listener.enable(null, handleTVKeyPress);
    //
    // return () => listener.disable();
  }, [focusedIndex]);

  const SelectablePoster = ({ item, index }) => {
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
            posterType={PosterType.PREVIEW}
            metadata={item}
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
          keyExtractor={(item) => item.id}
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
}
