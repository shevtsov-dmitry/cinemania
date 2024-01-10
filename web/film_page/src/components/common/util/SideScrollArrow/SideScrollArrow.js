export class SideScrollArrow {
    scrollBlock;
    constructor(scrollBlock) {
        this.scrollBlock = scrollBlock
    }
    scrollLeft(distance){
        this.scrollBlock.current.scrollLeft -= distance
    }

    scrollRight(distance){
        this.scrollBlock.current.scrollLeft += distance
    }

     hideArrowsLeaningScreen(leftArrowRef, rightArrowRef) {
        const block = this.scrollBlock.current

        if (block) {
            const isScrollAtMin = block.scrollLeft === 0
            const isScrollAtMax =
                block.scrollLeft >= block.scrollWidth - block.clientWidth

            if (isScrollAtMin) {
                leftArrowRef.current.style.visibility = 'hidden'
            } else {
                leftArrowRef.current.style.visibility = 'visible'
            }

            if (isScrollAtMax) {
                rightArrowRef.current.style.visibility = 'hidden'
            } else {
                rightArrowRef.current.style.visibility = 'visible'
            }
        }
    }
    hideShowArrowsOnHover(isBlockHovered, arrowsHolder) {
        let style = arrowsHolder.current.style
        isBlockHovered ? (style.display = 'block') : (style.display = 'none')
    }
}