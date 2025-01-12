import { FlatList, Text, View } from "react-native";
import { ContentMetadata } from "@/src/types/ContentMetadata";

/**
 *
 * @param {Object} props
 * @param {[string]} props.posterImage base64 poster image
 * @param {ContentMetadata} props.metadata
 * @returns {JSX.Element}
 */
export default function ContentPage({ posterImage, metadata }) {
  const BasicInfo = () => (
    <View id="main-container" className="w-1/3 h-2/3 text-white">
      <Text>{metadata.title}</Text>
      <View id="short-info-holder" className="flex gap-3">
        <Text>{metadata.releaseDate}</Text>
        <Text>{metadata.mainGenre}</Text>
        {/* TODO add watchtime */}
        {/* <Text>{ metadata.mainGenre }</Text> */}
        <Text>{metadata.age}+</Text>
      </View>
      <Text className="underline text-white" id="producer"></Text>
      <FlatList
        id="actors"
        data={metadata}
        keyExtractor={(metadata) => metadata.actors}
        renderItem={({ name, index }) => (
          <Text className="underline">{name}</Text>
        )}
      />
    </View>
  );

  return (
    <View
      className="min-w-screen min-h-screen"
      style={{
        background:
          "radial-gradient(circle, rgba(16,16,17,1) 46%, rgba(25,27,28,0.8379726890756303) 87%)",
      }}
    >
      <BasicInfo />
    </View>
  );
}
