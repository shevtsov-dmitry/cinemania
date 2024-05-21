import { Preview } from './Preview'
import { Header } from './Header'
import { DefaultCompilation } from './compilations/DefaultCompilation'

export default function Home() {
    return (
        <>
            <Preview />
            <DefaultCompilation />
        </>
    )
}
