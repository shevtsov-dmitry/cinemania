import {Link, Stack} from 'expo-router';
import {StyleSheet} from 'react-native';


export default function NotFoundScreen() {
    return (
        <div>
            <Stack.Screen options={{title: 'Oops!'}}/>
            <h2>This screen does not exist.</h2>
            <Link href="/" style={styles.link}>
                <h2>Go to home screen!</h2>
            </Link>
        </div>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        padding: 20,
    },
    link: {
        marginTop: 15,
        paddingVertical: 15,
    },
});
