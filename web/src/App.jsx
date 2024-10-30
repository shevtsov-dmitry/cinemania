import {Header} from './components/Main/Header'
import {Route, Routes} from 'react-router-dom'
import Home from './components/Main/Home'
import {VideoPlayer} from './components/VideoPlayer'
import {useSelector} from 'react-redux'
import {useEffect, useState} from 'react';

export default function App() {
    const videoPlayerState = useSelector((state) => state.videoPlayer)
    const videoId = videoPlayerState.videoId
    const [text, setText] = useState('');
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchText = async () => {
            try {
                const response = await fetch('https://lovemenot.ru:8443/');
                if (!response.ok) throw new Error('Failed to fetch data');
                const text = await response.text();
                setText(text);
            } catch (error) {
                setError(error.message);
            }
        };

        fetchText();
    }, []);

    return (
        <div className="min-w-100  h-dvh min-h-20 bg-neutral-800">
            <Header/>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/watch/:videoId" element={<VideoPlayer/>}/>
            </Routes>

            <div className="flex min-h-screen items-center justify-center bg-gray-900">
                <div className="max-w-2xl rounded-lg bg-gray-800 p-8 shadow-lg">
                    <h1 className="mb-4 text-center text-4xl font-bold text-white">
                        Сообщение с сервера
                    </h1>
                    {error ? (
                        <p className="text-xl text-red-500">{error}</p>
                    ) : (
                        <p className="text-center text-2xl leading-relaxed text-white">
                            {text || 'Загрузка...'}
                        </p>
                    )}
                </div>
            </div>

        </div>
    )
}
