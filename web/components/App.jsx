import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import { Header } from 'react-native/Libraries/NewAppScreen';
import Home from '@/components/Main/Home';
import { VideoPlayer } from '@/components/VideoPlayer';

let error = false;

export default function App() {
    return (
        <div className="min-w-100 h-dvh min-h-20 bg-neutral-800">
            <Header />
            <Home />
            {/*<VideoPlayer/>*/}
            {/*TODO use zustand instead*/}
            {/*<Routes>*/}
            {/*    <Route path="/" element={<Home />} />*/}
            {/*    <Route path="/watch/:videoId" element={<VideoPlayer />} />*/}
            {/*</Routes>*/}

            {/*TODO use different component for this */}
            <div className="flex min-h-screen items-center justify-center bg-gray-900">
                <div className="max-w-2xl rounded-lg bg-gray-800 p-8 shadow-lg">
                    <h1 className="mb-4 text-center text-4xl font-bold text-white">
                        Сообщение с сервера
                    </h1>
                    {error ? (
                        <p className="text-xl text-red-500">{error}</p>
                    ) : (
                        <p className="text-center text-2xl leading-relaxed text-white">
                            {'Загрузка...'}
                        </p>
                    )}
                </div>
            </div>
        </div>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#fff',
        alignItems: 'center',
        justifyContent: 'center'
    }
});
