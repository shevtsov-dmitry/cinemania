import Colors from "@/src/constants/Colors";
import Constants from "@/src/constants/Constants";
import useContentPageState from "@/src/state/contentPageState";
import { Ionicons } from "@expo/vector-icons";
import { BlurView } from "expo-blur";
import { useRouter } from "expo-router";
import React, { ReactElement, useEffect, useRef, useState } from "react";
import { StyleSheet, Image, Pressable, Text, View } from "react-native";
import BackSign from "../common/BackSign";
import ContentCreator from "@/src/types/ContentCreator";
import { useVideoPlayer, VideoPlayer, VideoView } from "expo-video";

interface ContentPageProps {
}

const ContentPage = ({}: ContentPageProps): ReactElement => {
  const [posterUrl, setPosterUrl] = useState<string>("");
  const { contentPageMetadata } = useContentPageState();
  const router = useRouter();

  const videoRef = useRef<VideoView>(null);

  useEffect(() => {
    fetchPoster();

    if (videoRef.current) {
      videoRef.current.setOnPlaybackStatusUpdate((status) => {
        console.log("Video status:", status);
      });
    }
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
          backgroundImage: Colors.TEAL_DARK_GRADIENT_BG_IMAGE
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
    "http://localhost:8443/api/v1/stream/67d47489eeda036a76103a6e/playlist";

  // This filmUrl is for your full-length film (if needed)
  const filmUrl =
    Constants.STREAMING_SERVER_URL +
    `/stream/${contentPageMetadata.standaloneVideoShow?.id}`;

  const styles = StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: "black"
    },
    video: {
      position: "absolute",
      top: 0,
      left: 0,
      right: 0,
      bottom: 0
    }
  });

  const FullscreenVideo = (): ReactElement => {
    const player = useVideoPlayer(trailerUrl, (player: VideoPlayer) => {
      player.loop = true;
      player.play();
    });

    return (
      <View style={styles.container}>
        <VideoView
          style={styles.video}
          player={player}
          allowsFullscreen
          allowsPictureInPicture
        />
      </View>
    );
  };

  return (
    <View className="relative h-screen w-screen flex-1 bg-cyan-800">
      <View className="fixed left-3 top-3 z-10 w-1/6 h-1/6">
        <BackSign />
      </View>
      <FullscreenVideo />
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

        <Text
          id={"film-title"}
          className="mb-4 text-center text-3xl font-bold text-white"
        >
          {contentPageMetadata.title}
        </Text>
        <Text
          id={"film-details"}
          className="mb-2 text-center text-lg text-white"
        >
          {contentPageMetadata.releaseDate} • {contentPageMetadata.country.name}{" "}
          • {contentPageMetadata.mainGenre.name} • Rating:{" "}
          {contentPageMetadata.rating} • {contentPageMetadata.age}+
        </Text>
        <Text id={"director"} className="mb-2 text-center text-lg text-white">
          Director: {contentPageMetadata.filmingGroup.director.name}{" "}
          {contentPageMetadata.filmingGroup.director.surname}
        </Text>
        <Text id={"actors"} className="mb-2 text-center text-lg text-white">
          Actors:{" "}
          {contentPageMetadata.filmingGroup.actors
            .slice(0, 3)
            .map(
              (actor: ContentCreator): string =>
                `${actor.name} ${actor.surname}`
            )
            .join(", ")}
          {contentPageMetadata.filmingGroup.actors.length > 3 ? "..." : ""}
        </Text>
        <Text
          id={"description"}
          className="mb-5 px-2 text-center text-base text-white"
        >
          {contentPageMetadata.description}
        </Text>

        <Pressable
          id={"play-button"}
          className="flex-row items-center rounded-md bg-red-600 px-5 py-2"
          onPress={() => {
            // Navigate to full film player (adjust based on your navigation setup)
            // Example with React Navigation:
            // navigation.navigate('VideoPlayer', { videoUrl: filmUrl });
          }}
        >
          <Ionicons name="play" size={24} color="white" />
          <Text className="text-lg text-white">Play</Text>
        </Pressable>
      </BlurView>
    </View>
  );
};

export default ContentPage;
