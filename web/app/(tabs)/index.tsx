import { Image, StyleSheet, Platform } from 'react-native';

// import { HelloWave } from "@/components/HelloWave";
// import ParallaxScrollView from "@/components/ParallaxScrollView";
// import { ThemedText } from "@/components/ThemedText";
// import { ThemedView } from "@/components/ThemedView";

export default function HomeScreen() {
    return (
        <div className="min-w-100 h-dvh min-h-20 bg-neutral-800">
            <Header />
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/watch/:videoId" element={<VideoPlayer />} />
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
    );
}
