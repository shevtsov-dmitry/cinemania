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
}