import { ReactElement, useEffect } from "react";

import { View } from "react-native";
import { Image, ImageBackground } from "expo-image";

interface PosterProps {
  id?: string;
  base64Image: string;
}

const Poster = ({ id, base64Image }: PosterProps): ReactElement => (
  <View className="w-full h-full ">
    <ImageBackground
      id={id}
      className="rounded-3xl w-full h-full absolute inset-0"
      source={{ uri: `data:image/jpeg;base64,${base64Image}` }}
      style={{ flex: 1 }}
    />
  </View>
);

export default Poster;
