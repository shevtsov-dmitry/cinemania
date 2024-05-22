import { useRef } from 'react'
import { SideArrows } from '../../common/util/SideArrows'

export function DefaultCompilation(props) {
    const scrollableDivRef = useRef()

    // useEffect(() => {
    //     if (!isPlayerOpened) {
    //         leftArrowRef.current.style.visibility = 'hidden'
    //     }
    // }, [])

    const initSize = 30 // get it from props or other way like fetch
    const compilation = new Array(initSize)
    for (let i = 0; i < initSize; i++) {
        compilation.push(<Element key={Math.random()} />)
    }

    return (
        <div className="flex flex-col justify-center">
            <div
                ref={scrollableDivRef}
                className="no-scrollbar relative overflow-x-scroll scroll-smooth p-2"
            >
                <div className="flex w-fit gap-4 overflow-scroll bg-fuchsia-500">
                    {compilation}
                </div>
            </div>
            <SideArrows scrollableDivRef={scrollableDivRef} />
        </div>
    )
}

function Element() {
    return <div className="h-44 w-72 overflow-hidden rounded bg-sky-100"></div>
}
