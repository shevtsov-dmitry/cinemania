function fillBlockWithPosters() {
    const content = []
    for (let i = 0; i < 15; i++) {
        content.push(<li className={'preview-poster-el hover:scale-105 transition-all'}></li>)
    }
    return content
}

export function Preview() {
    return (
        <>
            <h3 className={'p-2 text-2xl font-bold text-white'}>Новинки</h3>
            <div className="no-scrollbar relative overflow-x-scroll p-2">
                <ul id={'previews-sequence-block'} className="flex w-fit gap-4">
                    {fillBlockWithPosters()}
                </ul>
                <div className="centered-arrow-block centered-arrow-block-left">
                    <img src={"icons/left-arrow.png"} className={'arrow'}  alt={"scroll left"}/>
                </div>
                <div className="centered-arrow-block centered-arrow-block-right w-fit h-fit">
                    <img src={"icons/right-arrow.png"} className={'arrow'}  alt={"scroll right"}/>
                </div>
            </div>
        </>
    )
}
