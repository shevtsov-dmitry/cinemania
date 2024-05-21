import { Preview } from './Preview'
import { Header } from './Header'
import { DefaultCompilation } from './compilations/DefaultCompilation'

export default function Home() {
    return (
        <>
            <h3 className={'p-2 text-2xl font-bold text-white'}>Новинки</h3>
            <Preview />
            <h3 className={'p-2 text-2xl font-bold text-white'}>
                Вам может понравится
            </h3>
            <DefaultCompilation />
        </>
    )
}
