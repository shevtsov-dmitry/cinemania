import Preview from "@/src/components/home/Preview";
import React, { ReactElement } from "react";
import { Text } from "react-native";

const Home = (): ReactElement => {
  return (
    <>
      <Text className={"p-2 text-2xl font-bold text-white"}>Новинки</Text>
      <Preview />
      <Text className={"p-2 text-2xl font-bold text-white"}>
        Вам может понравится
      </Text>
    </>
  );
};

export default Home;
