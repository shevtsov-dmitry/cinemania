import { Header } from './components/Main/Header'
import { Preview } from './components/Main/Preview'
import { Route, Routes } from 'react-router-dom'
import FormAddFilm from './components/FormAddFilm/FormAddFilm'
import {useState} from "react";

export default function App() {

    return (
        <div className="min-w-100 h-dvh min-h-20 bg-neutral-800">
            <Header></Header>
            <Preview />
        </div>
    )
}
