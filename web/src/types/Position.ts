enum Position {
    DIRECTOR = 'DIRECTOR',
    ACTOR = 'ACTOR',
    PRODUCER = 'PRODUCER',
    OPERATOR = 'OPERATOR',
}

const PositionKindLocalized = {
    RU: {
        [Position.DIRECTOR]: 'Режиссёр',
        [Position.ACTOR]: 'Актёр',
        [Position.PRODUCER]: 'Продюсер',
        [Position.OPERATOR]: 'Оператор',
    },
}

export default Position
export { PositionKindLocalized }
