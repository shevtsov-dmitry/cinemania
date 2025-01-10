import { StyleSheet, Text, View } from "react-native";

import Header from "@/src/components/header/Header";
import Home from "@/src/components/home/Home";
import FormAddFilm from "@/src/components/admin/form-add-film/FormAddFilm";
import VideoPlayer from "@/src/components/video-player/VideoPlayer";
import PosterType from "./poster/PosterType";

let error = false;

export default function App() {
  return (
    <View className="min-w-100 h-dvh min-h-20 bg-neutral-800">
      <Header />
      {/* <FormAddFilm /> */}
      <Home />
      {/* <VideoPlayer /> */}
      {/*TODO use zustand instead*/}
      {/*<Routes>*/}
      {/*    <Route path="/" element={<Home />} />*/}
      {/*    <Route path="/watch/:videoId" element={<VideoPlayer />} />*/}
      {/*</Routes>*/}

      {/*TODO use different component for this */}
      <View className="flex min-h-screen items-center justify-center bg-gray-900">
        <View className="max-w-2xl rounded-lg bg-gray-800 p-8 shadow-lg">
          <Text className="mb-4 text-center text-4xl font-bold text-white">
            Сообщение с сервера
          </Text>
          {error ? (
            <Text className="text-xl text-red-500">{error}</Text>
          ) : (
            <Text className="text-center text-2xl leading-relaxed text-white">
              {"Загрузка..."}
            </Text>
          )}
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    alignItems: "center",
    justifyContent: "center",
  },
});
