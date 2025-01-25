/* eslint-disable */
import * as Router from 'expo-router';

export * from 'expo-router';

declare module 'expo-router' {
  export namespace ExpoRouter {
    export interface __routes<T extends string | object = string> {
      hrefInputParams: { pathname: Router.RelativePathString, params?: Router.UnknownInputParams } | { pathname: Router.ExternalPathString, params?: Router.UnknownInputParams } | { pathname: `/modal`; params?: Router.UnknownInputParams; } | { pathname: `/../components/App`; params?: Router.UnknownInputParams; } | { pathname: `/../components/home/Home`; params?: Router.UnknownInputParams; } | { pathname: `/../constants/Constants`; params?: Router.UnknownInputParams; } | { pathname: `/_sitemap`; params?: Router.UnknownInputParams; } | { pathname: `/+not-found`, params: Router.UnknownInputParams & {  } };
      hrefOutputParams: { pathname: Router.RelativePathString, params?: Router.UnknownOutputParams } | { pathname: Router.ExternalPathString, params?: Router.UnknownOutputParams } | { pathname: `/modal`; params?: Router.UnknownOutputParams; } | { pathname: `/../components/App`; params?: Router.UnknownOutputParams; } | { pathname: `/../components/home/Home`; params?: Router.UnknownOutputParams; } | { pathname: `/../constants/Constants`; params?: Router.UnknownOutputParams; } | { pathname: `/_sitemap`; params?: Router.UnknownOutputParams; } | { pathname: `/+not-found`, params: Router.UnknownOutputParams & {  } };
      href: Router.RelativePathString | Router.ExternalPathString | `/modal${`?${string}` | `#${string}` | ''}` | `/../components/App${`?${string}` | `#${string}` | ''}` | `/../components/home/Home${`?${string}` | `#${string}` | ''}` | `/../constants/Constants${`?${string}` | `#${string}` | ''}` | `/_sitemap${`?${string}` | `#${string}` | ''}` | { pathname: Router.RelativePathString, params?: Router.UnknownInputParams } | { pathname: Router.ExternalPathString, params?: Router.UnknownInputParams } | { pathname: `/modal`; params?: Router.UnknownInputParams; } | { pathname: `/../components/App`; params?: Router.UnknownInputParams; } | { pathname: `/../components/home/Home`; params?: Router.UnknownInputParams; } | { pathname: `/../constants/Constants`; params?: Router.UnknownInputParams; } | { pathname: `/_sitemap`; params?: Router.UnknownInputParams; } | `/+not-found` | { pathname: `/+not-found`, params: Router.UnknownInputParams & {  } };
    }
  }
}
