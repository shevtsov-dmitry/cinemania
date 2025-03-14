import Colors from "@/src/constants/Colors";
import React, { ReactElement, useEffect, useState } from "react";
import { Text, View } from "react-native";
import AdminPage from "../admin/AdminPage";
import TopPanel from "../top-panel/TopPanel";
import Preview from "./Preview";
import { Image } from "expo-image";

const Home = (): ReactElement => {
  return (
    <View
      className="min-h-screen w-screen"
      style={{
        backgroundImage: Colors.TEAL_DARK_GRADIENT_BG_IMAGE,
      }}
    >
      <TopPanel />
      <AdminPage />
      <Text className={"p-2 text-2xl font-bold text-white"}>Новинки</Text>
      <Preview />
      <Text className={"p-2 text-2xl font-bold text-white"}>
        Вам может понравится
      </Text>
      <Preview />
    </View>
  );
};

export default Home;
