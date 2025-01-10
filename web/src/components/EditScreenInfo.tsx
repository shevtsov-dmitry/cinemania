import React from 'react'

import { ExternalLink } from './ExternalLink'
import { MonoText } from './StyledText'
import { Text, View } from './Themed'

export default function EditScreenInfo({ path }: { path: string }) {
    return (
        <View>
            <View className="mx-12 items-center">
                <Text className="text-center text-lg">
                    Open up the code for this screen:
                </Text>

                <View className="my-2 rounded px-1">
                    <MonoText>{path}</MonoText>
                </View>

                <Text className="text-center text-lg">
                    Change any of the text, save the file, and your app will
                    automatically update.
                </Text>
            </View>

            <View className="mx-5 mt-4 items-center">
                <ExternalLink
                    className="py-4"
                    href="https://docs.expo.io/get-started/create-a-new-app/#opening-the-app-on-your-phonetablet"
                >
                    <Text className="text-center text-link">
                        Tap here if your app doesn't automatically update after
                        making changes
                    </Text>
                </ExternalLink>
            </View>
        </View>
    )
}
