import { useLocalSearchParams } from 'expo-router'
import React from 'react'
import FullscreenVideoWeb from '../components/video-player/FullscreenVideoWeb'

const FullscreenVideoPage = () => {
    const { url } = useLocalSearchParams<{ url?: string }>()

    return <FullscreenVideoWeb url={url!} />
}

export default FullscreenVideoPage
