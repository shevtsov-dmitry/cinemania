import Colors from "@/src/constants/Colors";
import Constants from "@/src/constants/Constants";
import useContentPageState from "@/src/state/contentPageState";
import { Ionicons } from "@expo/vector-icons";
import { ResizeMode, Video } from "expo-av";
import { BlurView } from "expo-blur";
import { useRouter } from "expo-router";
import React, { ReactElement, useEffect, useState } from "react";
import { Image, Pressable, Text, View } from "react-native";
import BackSign from "../common/BackSign";
import ContentCreator from "@/src/types/ContentCreator";

interface ContentPageProps {}

const ContentPage = ({}: ContentPageProps): ReactElement => {
  const [posterUrl, setPosterUrl] = useState<string>("");
  const { contentPageMetadata } = useContentPageState();
  const router = useRouter();

  useEffect(() => {
    fetchPoster();
  }, []);

  async function fetchPoster() {
    const url =
      Constants.STORAGE_URL +
      `/api/v0/posters/${contentPageMetadata?.poster?.id}`;
    const res = await fetch(url);
    const blob = await res.blob();
    setPosterUrl(URL.createObjectURL(blob));
  }

  if (!contentPageMetadata) {
    return (
      <View
        className="flex h-screen w-screen items-center justify-center gap-5"
        style={{
          backgroundImage: Colors.TEAL_DARK_GRADIENT_BG_IMAGE,
        }}
      >
        <Text className="text-5xl font-bold text-white">
          Выбранный видеофайл не найден.
        </Text>
        <Pressable
          className="w-52 rounded-xl bg-cyan-700 p-5 shadow hover:bg-cyan-500"
          onPress={() => router.back()}
        >
          <Text className="text-center text-3xl font-bold text-white">
            Вернуться
          </Text>
        </Pressable>
      </View>
    );
  }

  // Update the trailerUrl to point to the HLS playlist endpoint.
  // For example: http://localhost:8443/api/v1/stream/<trailer-id>/playlist
  // const trailerUrl = Constants.STREAMING_SERVER_URL + `/api/v1/stream/${contentPageMetadata.trailer?.id}/playlist`;
  const trailerUrl =
    "http://localhost:8443/api/v1/stream/67c9e5ee04383653f03a4e6b/playlist";

  // This filmUrl is for your full-length film (if needed)
  const filmUrl =
    Constants.STREAMING_SERVER_URL +
    `/stream/${contentPageMetadata.standaloneVideoShow?.id}`;

  useEffect(() => {
    console.log("Trailer URL:", trailerUrl);
  }, [trailerUrl]);

  return (
    <View className="relative h-screen w-screen flex-1 bg-cyan-800">
      {/* Background Video with HLS Stream */}
      <View className="fixed left-3 top-3 z-10">
        <BackSign />
      </View>
      <Video
        source={{ uri: trailerUrl }}
        // Uncomment the posterSource below if you want to display a poster image
        // posterSource={{ uri: posterUrl }}
        rate={1.0}
        volume={1.0}
        isMuted={true}
        resizeMode={ResizeMode.COVER}
        shouldPlay
        isLooping
        className="absolute inset-0 h-full w-full"
      />

      {/* Overlay with Blur Effect and Metadata */}
      <BlurView
        intensity={50}
        tint="dark"
        className="absolute inset-0 flex h-full w-full items-center justify-center p-5"
      >
        <Image
          className="mb-[2%] h-52 w-52"
          alt="Ошибка загрузки постера"
          source={{ uri: posterUrl }}
        />

        {/* Film Title */}
        <Text className="mb-4 text-center text-3xl font-bold text-white">
          {contentPageMetadata.title}
        </Text>

        {/* Film Details */}
        <Text className="mb-2 text-center text-lg text-white">
          {contentPageMetadata.releaseDate} • {contentPageMetadata.country.name}{" "}
          • {contentPageMetadata.mainGenre.name} • Rating:{" "}
          {contentPageMetadata.rating} • {contentPageMetadata.age}+
        </Text>

        {/* Director */}
        <Text className="mb-2 text-center text-lg text-white">
          Director: {contentPageMetadata.filmingGroup.director.name}{" "}
          {contentPageMetadata.filmingGroup.director.surname}
        </Text>

        {/* Actors (limited to first 3 for brevity) */}
        <Text className="mb-2 text-center text-lg text-white">
          Actors:{" "}
          {contentPageMetadata.filmingGroup.actors
            .slice(0, 3)
            .map(
              (actor: ContentCreator): string =>
                `${actor.name} ${actor.surname}`,
            )
            .join(", ")}
          {contentPageMetadata.filmingGroup.actors.length > 3 ? "..." : ""}
        </Text>

        {/* Description */}
        <Text className="mb-5 px-2 text-center text-base text-white">
          {contentPageMetadata.description}
        </Text>

        {/* Play Button for full film (if needed) */}
        <Pressable
          className="flex-row items-center rounded-md bg-red-600 px-5 py-2"
          onPress={() => {
            // Navigate to full film player (adjust based on your navigation setup)
            // Example with React Navigation:
            // navigation.navigate('VideoPlayer', { videoUrl: filmUrl });
          }}
        >
          <Ionicons name="play" size={24} color="white" />
          <Text className="ml-2 text-lg text-white">Play</Text>
        </Pressable>
      </BlurView>
    </View>
  );
};

export default ContentPage;
