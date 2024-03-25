import {Header} from './components/Main/Header'
import {Route, Routes} from 'react-router-dom'
import Home from "./components/Main/Home";
import {VideoPlayer} from "./components/VideoPlayer";

export default function App() {


    return (
        <div className="min-w-100 h-dvh min-h-20 bg-neutral-800">
            <Header/>

            <Routes>
                <Route
                    path="/"
                    element={<Home/>}
                />
                <Route path="/watch" element={<VideoPlayer/>}/>
            </Routes>
        </div>
    )
}
