import { ReactElement, useEffect } from "react";

import { Image, View } from "react-native";
import CompilationKind from "../compilations/CompilationKind";

interface PosterProps {
  id?: string;
  compilationKind: CompilationKind;
  base64Image: string;
}

const Poster = ({
  id,
  compilationKind = CompilationKind.DEFAULT,
  base64Image,
}: PosterProps): ReactElement => {
  return (
    <View
      id={id}
      className={`${compilationKind === CompilationKind.PREVIEW && "h-[200px] w-[150px]"} ${compilationKind === CompilationKind.DEFAULT && "h-96 w-64"} `}
    >
      <Image
        className={`h-full w-full rounded-3xl`}
        source={{ uri: `data:image/jpeg;base64,${base64Image}` }}
        alt={id}
      />
    </View>
  );
};

export default Poster;
