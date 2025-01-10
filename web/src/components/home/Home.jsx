import Preview from "@/src/components/home/Preview";
import DefaultCompilation from "@/src/components/compilations/DefaultCompilation";
import { useEffect, useState } from "react";
import { Text, View, Image } from "react-native";
import Constants from "@/src/constants/Constants";

/**
 *
 * @returns {JSX.Element}
 */
export default function Home() {
  return (
    <>
      <Text className={"p-2 text-2xl font-bold text-white"}>Новинки</Text>
      <Preview />
      <Text className={"p-2 text-2xl font-bold text-white"}>
        Вам может понравится
      </Text>
      <View id="preview-posters-holder"></View>
    </>
  );
}
