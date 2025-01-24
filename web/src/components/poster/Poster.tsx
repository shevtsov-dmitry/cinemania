import { ReactElement } from "react";

import ContentDetails from "@/src/interfaces/ContentDetails";
import { Image, View } from "react-native";
import CompilationKind from "../compilations/CompilationKind";

interface PosterProps {
  compilationKind: CompilationKind;
  metadata: ContentDetails;
  imageUrl: string;
}

/**
 * @note image URL can be inserted in the `<img src="...">` tag to display image
 */
const Poster = ({
  compilationKind = CompilationKind.DEFAULT,
  metadata,
  imageUrl,
}: PosterProps): ReactElement => {
  return (
    <View
      className={`
      ${compilationKind === CompilationKind.PREVIEW && "h-[200px] w-[150px]"}
      ${compilationKind === CompilationKind.DEFAULT && "h-96 w-64"}
      `}
    >
      <Image
        className={`w-full h-full rounded-3xl `}
        source={{ uri: imageUrl }}
      />
    </View>
  );
};

//   {/*<div className="postersInfo">*/}
//   {/*    {isPosterHovered && (*/}
//   {/*        <div>*/}
//   {/*            <ContentInformation/>*/}
//   {/*            <div className="flex w-64 justify-center">*/}
//   {/*                /!* <Link *!/*/}
//   {/*                /!*     className="absolute bottom-0 z-10 mb-10" *!/*/}
//   {/*                /!*     to={`/watch/${metadata.videoId}`} *!/*/}
//   {/*                /!* > *!/*/}
//   {/*                <button*/}
//   {/*                    className="select-none rounded-3xl bg-pink-700 p-4 font-sans text-2xl font-bold text-white opacity-75 hover:opacity-95"*/}
//   {/*                    // onClick={() => {};*/}
//   {/*                    //     // TODO rewrite to Zustand*/}
//   {/*                    //     // dispatch(setVideoId(metadata.videoId))*/}
//   {/*                    // }*/}
//   {/*                >*/}
//   {/*                    Смотреть*/}
//   {/*                </button>*/}
//   {/*                /!* </Link> *!/*/}
//   {/*            </div>*/}
//   {/*        </div>*/}
//   {/*    )}*/}
//   {/*</div>*/}
// {/* </Image> */}

export default Poster;
