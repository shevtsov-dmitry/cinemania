import { Header } from './components/Header'
import { Preview } from './components/Preview'
import {VideoMaterialInfo} from "./components/VideoMaterialInfo";

export default function App() {
    return (
        <body className="min-w-100 h-dvh min-h-20 bg-neutral-800">
            <Header/>
            {/*<Preview/>*/}
        <VideoMaterialInfo/>
        </body>
    )
}
