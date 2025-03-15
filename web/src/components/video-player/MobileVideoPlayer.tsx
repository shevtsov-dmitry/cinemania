import React, {useEffect} from 'react';
import {useVideoPlayer, VideoView} from 'expo-video';
import {useEvent} from "expo";
import {View, Text} from "react-native";
import uri from "ajv/lib/runtime/uri";

interface MobileVideoPlayerProps {
    url: string;
    setLogMessage: (message: string) => void
}

const MobileVideoPlayer = ({url, setLogMessage}: MobileVideoPlayerProps) => {

    const player = useVideoPlayer({uri: url}, player => {
        player.loop = true;
        player.play();
    });

    const {isPlaying} = useEvent(player, 'playingChange', {isPlaying: player.playing});

    return (
        <View>
            <Text>is playing? {`${isPlaying}`}</Text>
            <VideoView style={{width: "100%", height: "400"}} player={player} allowsFullscreen allowsPictureInPicture/>
        </View>
    );
};

export default MobileVideoPlayer;