import { useRef } from "react";
import { SectionList, Text, View } from "react-native";
import Poster from "@/src/components/poster/Poster";

/**
 * Film posters compilation with related metadata.
 * @param {Object} props
 * @param {ContentMetadata[]} props.metadataList - list of content metadata for each poster fetched from server
 * @returns {JSX.Element}
 */
export default function DefaultCompilation({ metadataList }) {
  const scrollableDivRef = useRef();

  // useEffect(() => {
  //     if (!isPlayerOpened) {
  //         leftArrowRef.current.style.visibility = 'hidden'
  //     }
  // }, [])

  // <SectionList
  //   className="flex w-fit gap-4 overflow-scroll bg-fuchsia-500"
  //   horizontal
  //   data={metadataList}
  //   renderItem={({ metadata, idx }) => (
  //     <Poster key={idx} metadata={metadata} />
  //   )}
  // />
  return (
    <View className="flex flex-col justify-center">
      <View
        ref={scrollableDivRef}
        className="no-scrollbar relative overflow-x-scroll scroll-smooth p-2"
      >
        {metadataList.length !== 0 ? (
          // TODO insert here Section List for posters display
          <View />
        ) : (
          <Text>Постеры загружаются...</Text>
        )}
      </View>
      {/* TODO use scrollable View for mobile compatible devices */}
      {/* <SideArrows scrollableDivRef={scrollableDivRef} /> */}
    </View>
  );
}
