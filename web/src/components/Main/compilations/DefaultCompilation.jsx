export function DefaultCompilation(props) {
    const initSize = 10 // get it from props or other way like fetch
    const compilation = new Array(initSize)
    for (let i = 0; i < initSize; i++) {
        compilation.push(<Element />)
    }
    return <div className="flex gap-4 bg-fuchsia-500">{compilation}</div>
}

function Element() {
    return <div className="h-32 w-52 rounded bg-sky-100"></div>
}
