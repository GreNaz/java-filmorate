# Filmorate
![](https://img.shields.io/badge/database-H2Database-blue)
![](https://img.shields.io/badge/language-Java-orange)
![](https://img.shields.io/badge/build_automation_tool-Maven-red)
![](https://img.shields.io/badge/framework-Spring_boot-green)

------
#### Project is a storage for films with opportunity for users to make likes, friends, reviews and etc.

#### Приложение умеет:

1. Full Rest API для работы с пользователями, фильмами, жанрами и директорами фильмов.
2. Хранение данных в базе данных H2.
3. Входные данные проходят валидацию.
4. Реализовано логирование на Slf4j.
5. Основные свойства фильма: название, описание, дата релиза, продолжительность.
6. Дополнительные характеристики фильма: рейтинг на основе кол-ва лайков от пользователей, возрастные рекомендации для просмотра, жанр фильма.
7. У фильмов реализовано свойство - режиссёр фильма с функциональностью:
    - вывод всех фильмов режиссёра, отсортированных по количеству лайков.
    - вывод всех фильмов режиссёра, отсортированных по годам.
8. Основные свойства пользователя: e-mail, логин, имя, день рождения.
9. Дополнительные связи пользователя: друзья, отзывы и лайки фильмам.
10. В приложении есть возможность делать отзывы на фильмы. Добавленные отзывы имеют рейтинг и несколько дополнительных характеристик.
11. В приложении есть возможность просмотра последних событий на платформе — добавления в друзья, удаления из друзей, лайки и отзывы, которые оставили друзья пользователя.
12. В приложении есть возможность вывода общих с другом фильмов с сортировкой по их популярности.
13. В приложении есть возможность выводить топ-N фильмов по количеству лайков с фильтрацией по жанру или году.
14. Реализована простая рекомендательная система для фильмов на базе интересов и лайков друзей.
------
#### Приложение написано на Java. Пример кода:

```java
public class Practicum {
    public static void main(String[] args) {
    }
}
```

------
#### Примеры SQL запросов:
Запрос фильма с MPA = "G":
<pre>
SELECT films.*, m.*
FROM   films 
JOIN   mpa m ON m.mpa_id = films.mpa_id
WHERE  m.name = 'G';
</pre>
Запрос 10 наиболее популярных фильмов:
<pre>
SELECT    films.FILM_ID, films.name, description, release_date, duration, rate, m.mpa_id, m.name
FROM      films
LEFT JOIN films_likes fl ON films.FILM_ID = fl.film_id
LEFT JOIN mpa m on m.MPA_ID = films.mpa_id
GROUP BY  films.FILM_ID, fl.film_id IN (
      SELECT film_id
      FROM   films_likes
      )
ORDER BY COUNT(fl.film_id) DESC
LIMIT 10;
</pre>
------
#### ER-диаграмма:
![ER-диаграмма](ER-diagram.png)