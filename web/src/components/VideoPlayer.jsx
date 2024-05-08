import React, { useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link } from 'react-router-dom'
import { setVideoId } from '../store/videoPlayerSlice'

export function VideoPlayer() {
    const dispatch = useDispatch()
    const videoPlayerState = useSelector((state) => state.videoPlayer)
    let videoId = videoPlayerState.videoId

    // FIXME should find the better way to remember state. Maybe with browser 2"cache.
    if (videoId === '') {
        videoId = window.location.pathname.replace('/watch/', '')
        dispatch(setVideoId(videoId))
    }

    return (
        <div className="flex h-fit w-dvw flex-col justify-center">
            <video
                className="h-fit w-fit"
                controls
                autoPlay={true}
                muted={true}
            >
                <source
                    src={`${process.env.REACT_APP_SERVER_URL}:8081/videos/stream/start/${videoId}`}
                    type="video/mp2t"
                />
                Your browser does not support the video tag.
            </video>
        </div>
    )
}
