import { useState } from "react";
import { View, Text, TextInput, Button, Image, Alert } from "react-native";
import { launchImageLibrary } from "react-native-image-picker";

type UserPic = {
  id: string;
  contentType: string;
  filename: string;
  personCategory: string;
};

type ContentCreator = {
  fullname: string;
  fullnameEng: string;
  bornPlace: string;
  heightMeters: number;
  age: number;
  userPic: UserPic | null;
  birthDate: string; // Format: dd.MM.yyyy
  deathDate: string | null; // Optional
  isDead: boolean;
};

const FormAddCreator = (): React.ReactElement => {
  const [fullname, setFullname] = useState("");
  const [fullnameEng, setFullnameEng] = useState("");
  const [bornPlace, setBornPlace] = useState("");
  const [heightMeters, setHeightMeters] = useState(0);
  const [age, setAge] = useState(0);
  const [birthDate, setBirthDate] = useState("");
  const [deathDate, setDeathDate] = useState("");
  const [isDead, setIsDead] = useState(false);
  const [userPic, setUserPic] = useState<UserPic | null>(null);

  const handleUploadImage = async () => {
    const result = await launchImageLibrary({ mediaType: "photo" });

    if (result.assets && result.assets.length > 0) {
      const image = result.assets[0];

      const formData = new FormData();
      formData.append("image", {
        uri: image.uri,
        type: image.type,
        name: image.fileName,
      } as any);
      formData.append("personCategory", "USER");

      try {
        const response = await fetch("http://your-backend-url/upload", {
          method: "POST",
          body: formData,
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });

        if (!response.ok) {
          throw new Error("Failed to upload image");
        }

        const data: UserPic = await response.json();
        setUserPic(data);
        Alert.alert("Image uploaded successfully");
      } catch (error: any) {
        Alert.alert("Failed to upload image", error.message);
      }
    }
  };

  const handleSubmit = async () => {
    if (!fullname || !fullnameEng || !userPic || !birthDate) {
      Alert.alert("Please fill out all required fields and upload an image.");
      return;
    }

    const newCreator: ContentCreator = {
      fullname,
      fullnameEng,
      bornPlace,
      heightMeters,
      age,
      userPic,
      birthDate,
      deathDate: isDead ? deathDate : null,
      isDead,
    };

    try {
      const response = await fetch("http://your-backend-url/creators", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(newCreator),
      });

      if (!response.ok) {
        throw new Error("Failed to add creator");
      }

      Alert.alert("Creator added successfully");
    } catch (error: any) {
      Alert.alert("Failed to add creator", error.message);
    }
  };

  return (
    <View className={"p-5"}>
      <Text className={"text-xl font-bold mb-4"}>Add Content Creator</Text>

      <TextInput
        className={"border p-2 mb-4"}
        placeholder="Full Name"
        value={fullname}
        onChangeText={setFullname}
      />

      <TextInput
        className={"border p-2 mb-4"}
        placeholder="Full Name (English)"
        value={fullnameEng}
        onChangeText={setFullnameEng}
      />

      <TextInput
        className={"border p-2 mb-4"}
        placeholder="Born Place"
        value={bornPlace}
        onChangeText={setBornPlace}
      />

      <TextInput
        className={"border p-2 mb-4"}
        placeholder="Height (meters)"
        keyboardType="numeric"
        value={heightMeters.toString()}
        onChangeText={(text) => setHeightMeters(parseFloat(text) || 0)}
      />

      <TextInput
        className={"border p-2 mb-4"}
        placeholder="Age"
        keyboardType="numeric"
        value={age.toString()}
        onChangeText={(text) => setAge(parseInt(text, 10) || 0)}
      />

      <TextInput
        className={"border p-2 mb-4"}
        placeholder="Birth Date (dd.MM.yyyy)"
        value={birthDate}
        onChangeText={setBirthDate}
      />

      {isDead && (
        <TextInput
          className={"border p-2 mb-4"}
          placeholder="Death Date (dd.MM.yyyy)"
          value={deathDate}
          onChangeText={setDeathDate}
        />
      )}

      <View className={"flex-row items-center mb-4"}>
        <Text>Is Dead:</Text>
        <Button title={isDead ? "Yes" : "No"} onPress={() => setIsDead(!isDead)} />
      </View>

      <Button title="Upload Image" onPress={handleUploadImage} />

      {userPic && (
        <View className={"my-4"}>
          <Text>Uploaded Image:</Text>
          <Image
            source={{ uri: userPic.filename }}
            className={"w-32 h-32 border"}
          />
        </View>
      )}

      <Button title="Submit" onPress={handleSubmit} />
    </View>
  );
};

export default FormAddCreator;