import { useEffect, useState } from "react";

import ContentMetadata from "@/src/types/ContentMetadata";
import { View, Image, Pressable } from "react-native";
import PosterType from "../compilations/PosterType";

/**
 *
 * @param {Object} props
 * @param {PosterType} props.posterType - predefined PosterType (enum)
 * @param {ContentMetadata} props.metadata - list of content metadata for each poster fetched from server
 * @param {string} props.imageUrl image URL which can be inserted in the `<img src="...">` tag to display image
 * @returns {JSX.Element}
 */
export default function Poster({
  posterType = PosterType.DEFAULT,
  metadata,
  imageUrl,
}) {
  return (
    <View
      className={`
      ${posterType === PosterType.PREVIEW && "h-[200px] w-[150px]"}
      ${posterType === PosterType.DEFAULT && "h-96 w-64"}
      `}
    >
      <Image
        className={`w-full h-full rounded-3xl `}
        // source={{
        //   uri: `data:image/jpeg;base64,${base64}`,
        // }}
        source={{ uri: imageUrl }}
      />
    </View>
  );
}

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
