import React, { useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link } from 'react-router-dom'
import { setVideoId } from '../store/videoPlayerSlice'

export function VideoPlayer() {
    const dispatch = useDispatch()
    const videoPlayerState = useSelector((state) => state.videoPlayer)
    let videoId = videoPlayerState.videoId

    // FIXME should find the better way to remember state. Maybe with browser 2" cache.
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
                    // src={`${process.env.REACT_APP_SERVER_URL}:8081/videos/stream/start/${videoId}`}
                    src="https://r430.kujo-jotaro.com/blue-archive/7.720.bf1ef4ddeef52224.mp4?hash1=b76ae21a3d61880273a451f911897308&hash2=5fd10c3131f3b0c5768496109f480c57"
                    type="video/mp4"
                />
                Your browser does not support the video tag.
            </video>
        </div>
    )
}
