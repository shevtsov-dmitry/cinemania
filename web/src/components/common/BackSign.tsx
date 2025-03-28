import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { Pressable } from "react-native";

interface BackSignProps {
  color?: string;
  size?: number;
  bgColor?: string;
}

const BackSign = ({
  color = "black",
  size = 24,
  bgColor = "white",
}: BackSignProps) => {
  const router = useRouter();

  return (
    <Pressable
      className={`rounded-[50%] flex items-center justify-center py-5 w-fit h-fit hover:cursor-pointer bg-${bgColor}`}
      onPress={() => router.back()}
    >
      <Ionicons name="arrow-back" size={size} color={color} />
    </Pressable>
  );
};

export default BackSign;
