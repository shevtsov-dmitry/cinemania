import React from "react";
import Constants from "@/src/constants/Constants";
import { View, Text, Pressable } from "react-native";
import { Video } from "expo-av";
import { BlurView } from "expo-blur";
import { Ionicons } from "@expo/vector-icons";
import { useEffect } from "react";
import { useState } from "react";

const ContentPage = () => {
  const [posterUrl, setPosterUrl] = useState < string > "";

  const { metadata } = useFilmPageState();

  // Handle case where metadata is unavailable
  if (!metadata) {
    return null; // Or replace with a loading indicator, e.g., <ActivityIndicator />
  }

  // Construct URLs for poster, trailer, and full film (customize base URL as needed)
  const trailerUrl =
    Constants.STORAGE_URL + `/api/v0/trailers/${metadata.trailer?.id}`;
  const filmUrl =
    Constants.STORAGE_URL +
    `/api/v0/videos/${metadata.standaloneVideoShow?.id}`;

  useEffect(() => {
    async function fetchPoster() {
      const res = fetch(
        Constants.STORAGE_URL + `/api/v0/posters/${metadata.poster?.id}`,
      );
      // TODO display poster
    }
  }, []);

  return (
    <View className="flex-1 bg-black relative">
      {/* Background Video with Poster */}
      <Video
        source={{ uri: trailerUrl }}
        posterSource={{ uri: posterUrl }}
        rate={1.0}
        volume={1.0}
        isMuted={true} // Muted to avoid unexpected sound
        resizeMode="cover"
        shouldPlay // Auto-play the trailer
        isLooping // Loop the trailer
        className="absolute inset-0 w-full h-full"
      />

      {/* Overlay with Blur Effect */}
      <BlurView
        intensity={50}
        tint="dark"
        className="absolute inset-0 w-full h-full flex justify-center items-center p-5"
      >
        {/* Film Title */}
        <Text className="text-white text-3xl font-bold text-center mb-4">
          {metadata.title}
        </Text>

        {/* Film Details */}
        <Text className="text-white text-lg text-center mb-2">
          {metadata.releaseDate} • {metadata.country} • {metadata.mainGenre} •
          Rating: {metadata.rating} • {metadata.age}+
        </Text>

        {/* Director */}
        <Text className="text-white text-lg text-center mb-2">
          Director: {metadata.filmingGroup.director.name}{" "}
          {metadata.filmingGroup.director.surname}
        </Text>

        {/* Actors (limited to first 3 for brevity) */}
        <Text className="text-white text-lg text-center mb-2">
          Actors:{" "}
          {metadata.filmingGroup.actors
            .slice(0, 3)
            .map((actor) => `${actor.name} ${actor.surname}`)
            .join(", ")}
          {metadata.filmingGroup.actors.length > 3 ? "..." : ""}
        </Text>

        {/* Description */}
        <Text className="text-white text-base text-center mb-5 px-2">
          {metadata.description}
        </Text>

        {/* Play Button */}
        <Pressable
          className="flex-row items-center bg-red-600 py-2 px-5 rounded-md"
          onPress={() => {
            // Navigate to full film player (adjust based on your navigation setup)
            console.log("Navigate to film player with URL:", filmUrl);
            // Example with React Navigation:
            // navigation.navigate('VideoPlayer', { videoUrl: filmUrl });
          }}
        >
          <Ionicons name="play" size={24} color="white" />
          <Text className="text-white text-lg ml-2">Play Film</Text>
        </Pressable>
      </BlurView>
    </View>
  );
};

export default ContentPage;
