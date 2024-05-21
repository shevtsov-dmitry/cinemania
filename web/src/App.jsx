import { Header } from './components/Main/Header'
import { Route, Routes } from 'react-router-dom'
import Home from './components/Main/Home'
import { VideoPlayer } from './components/VideoPlayer'
import { useSelector } from 'react-redux'

export default function App() {
    const videoPlayerState = useSelector((state) => state.videoPlayer)
    const videoId = videoPlayerState.videoId
    // const videoPlayerPath = `/watch/${videoId}`
    return (
        <div className="min-w-100  h-dvh min-h-20 bg-neutral-800">
            <Header />
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/watch/:videoId" element={<VideoPlayer />} />
            </Routes>
        </div>
    )
}
