import React, { ReactElement, useEffect, useRef, useState } from "react";
import {
  Animated,
  FlatList,
  InteractionManager,
  Text,
  StyleSheet,
  TouchableOpacity,
  View,
} from "react-native";
import CompilationKind from "./CompilationKind";
import Poster from "@/src/components/poster/Poster";
import ContentMetadata from "@/src/types/ContentMetadata";
import { useRouter } from "expo-router";
import useContentPageState from "@/src/state/contentPageState";
import { BlurView } from "expo-blur";
import Base64WithId from "@/src/types/Base64WithId";

interface CompilationProps {
  postersWithIds: Base64WithId[];
  metadataList: ContentMetadata[];
  errmes?: string;
}

const Compilation = ({
  postersWithIds,
  metadataList,
  errmes = "постеры загружаются...",
}: CompilationProps): ReactElement => {
  const [focusedIndex, setFocusedIndex] = useState<number>(0);
  const [selectedIndex, setSelectedIndex] = useState<number>();

  const router = useRouter();

  const scaleAnim = useRef(new Animated.Value(1)).current;

  const { setContentPageMetadata } = useContentPageState();

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

  useEffect(() => {
    const listener = InteractionManager.createInteractionHandle();
    // TODO execute action on tv remote controller button press
    return () => InteractionManager.clearInteractionHandle(listener);
  }, [focusedIndex]);

  interface SelectablePosterProps {
    metadata: ContentMetadata;
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
        onPress={() => {
          setSelectedIndex(index);
          setContentPageMetadata(metadata);
          router.push("/content");
        }}
        onFocus={() => handleFocus(index)}
        onBlur={handleBlur}
      >
        <View className={`${isFocused || (isSelected && "scale-150")}`}>
          <Poster
            id={postersWithIds[index].id}
            compilationKind={CompilationKind.PREVIEW}
            base64Image={postersWithIds[index].image}
          />
        </View>
      </TouchableOpacity>
    );
  };

  return (
    <>
      <BlurView intensity={30} tint="prominent" style={styles.blurContainer}>
        {postersWithIds.length !== 0 ? (
          <FlatList
            horizontal
            contentContainerStyle={styles.flatListContent}
            data={metadataList}
            keyExtractor={(item) => item.id}
            renderItem={({ item, index }) => (
              <SelectablePoster metadata={item} index={index} />
            )}
          />
        ) : (
          <Text className={"text-1xl font-bold text-white opacity-20"}>
            {errmes}
          </Text>
        )}
      </BlurView>
    </>
  );
};

const styles = StyleSheet.create({
  backgroundImage: {
    flex: 1,
    width: "100%",
    height: "100%",
  },
  blurContainer: {
    flex: 1,
    padding: 15,
    borderRadius: 15,
    justifyContent: "center",
  },
  flatListContent: {
    flexDirection: "row",
    gap: 16,
    padding: 12,
    borderRadius: 10,
  },
});

export default Compilation;
