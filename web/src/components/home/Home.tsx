import Colors from "@/src/constants/Colors";
import React, { ReactElement, useEffect, useState } from "react";
import { Text, View } from "react-native";
import AdminPage from "../admin/AdminPage";
import TopPanel from "../top-panel/TopPanel";
import Preview from "./Preview";
import { Image } from "expo-image";
import { VideoView, useVideoPlayer } from "expo-video";
import MobileVideoPlayer from "../video-player/MobileVideoPlayer";
import WebVideoPlayer from "../video-player/WebVideoPlayer";

let trailerUrl =
  "http://localhost:8443/api/v1/stream/67d47489eeda036a76103a6e/playlist";

// trailerUrl =
//   "http://localhost:8443/api/v1/stream/67d47489eeda036a76103a6e/chunk/index0.ts";

const Home = (): ReactElement => {
  return (
    <View
      className="min-h-screen w-screen"
      style={{
        backgroundImage: Colors.TEAL_DARK_GRADIENT_BG_IMAGE,
      }}
    >
      <WebVideoPlayer url={trailerUrl} />
      {/* <MobileVideoPlayer url={trailerUrl} /> */}
      {/* <TopPanel /> */}
      {/* <AdminPage /> */}
      {/* <Text className={"p-2 text-2xl font-bold text-white"}>Новинки</Text> */}
      {/* <Preview /> */}
      {/* <Text className={"p-2 text-2xl font-bold text-white"}> */}
      {/*   Вам может понравится */}
      {/* </Text> */}
    </View>
  );
};

export default Home;
