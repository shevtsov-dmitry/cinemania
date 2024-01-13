import { Header } from './components/main/Header'
import { Preview } from './components/main/Preview'
import { Route, Router } from 'react-router-dom'

export default function App() {
    return (
        <div className="min-w-100 h-dvh min-h-20 bg-neutral-800">
            <Header />
            <Preview />
        </div>
    )
}