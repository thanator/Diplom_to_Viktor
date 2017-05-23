var fs = require('fs');
var express = require('express');
var Handlebars = require('handlebars');

function resourcePath(name) {
    return 'src/main/resources/' + name;
}

function templatePath(name) {
    return resourcePath('templates/' + name);
}

function readTemplate(name) {
    return fs.readFileSync(templatePath(name)).toString().replace(/\.html\.hbs/g, '');
}


var routes = {
    '/': {
        'template': 'index.html.hbs',
        'data': {
            'page_title': 'Главная страница',
            'user_name': 'admin',
            'is_admin': true
        }
    },
    '/home': {
        'template': 'home.html',
        'data': {
        }
    },
    '/rules': {
        'template': 'rules.html',
        'data': {
        }
    },
    '/auth/login': {
        'template': 'login.html.hbs',
        'data': {
            'page_title': 'Вход'
        }
    },
    '/auth/register': {
        'template': 'register.html.hbs',
        'data': {
            'page_title': 'Регистрация'
        }
    },
    '/admin/users': {
        'template': 'admin_users.html.hbs',
        'data': {
            'page_title': 'Ученики',
            'user_name': 'admin',
            'users': [
                {
                    'index': '1',
                    'name': 'admin',
                    'birthday': '01.01.2007',
                    'sex': 'Ж',
                    'group': 'Админ',
                    'login': 'admin'
                },
                {
                    'index': '1',
                    'name': 'admin',
                    'birthday': '01.01.2007',
                    'sex': 'Ж',
                    'group': 'Админ',
                    'login': 'admin'
                },
                {
                    'index': '1',
                    'name': 'admin',
                    'birthday': '01.01.2007',
                    'sex': 'Ж',
                    'group': 'Админ',
                    'login': 'admin'
                }
            ]
        }
    },
    '/admin/users/new': {
        'template': 'admin_users_new.html.hbs',
        'data': {
            'page_title': 'Новый пользователь',
            'user_name': 'admin'
        }
    },
    '/admin/tests': {
        'template': 'admin_tests.html.hbs',
        'data': {
            'page_title': 'Тестирования',
            'user_name': 'admin',
            'tests': [
                {
                    'id': '1',
                    'name': 'Тест нумбер уан',
                    'task_count': '0',
                    'user_count': '0'
                },
                {
                    'id': '1',
                    'name': 'Тест нумбер уан',
                    'task_count': '0',
                    'user_count': '0'
                },
                {
                    'id': '1',
                    'name': 'Тест нумбер уан',
                    'task_count': '0',
                    'user_count': '0'
                }
            ]
        }
    },
    '/admin/tests/1/tasks': {
        'template': 'admin_tasks.html.hbs',
        'data': {
            'page_title': 'Билеты к "Тест нумбер уан"',
            'user_name': 'admin',
            'test': {
                'id': '1'
            },
            'tasks': [
                {
                    'id': '1',
                    'text': 'Текст билета',
                    'answers': [
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': true
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        }
                    ]
                },
                {
                    'id': '1',
                    'text': 'Текст билета',
                    'answers': [
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': true
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        }
                    ]
                },
                {
                    'id': '1',
                    'text': 'Текст билета',
                    'answers': [
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': true
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        }
                    ]
                }
            ]
        }
    },
    '/admin/tests/1/tasks/new': {
        'template': 'admin_new_task.html.hbs',
        'data': {
            'page_title': 'Новый билет к "Тест нумбер уан"',
            'user_name': 'admin',
            'test': {
                'id': '1'
            }
        }
    },
    '/admin/tests/1/users': {
        'template': 'admin_users.html.hbs',
        'data': {
            'page_title': 'Ученики, прошедшие "Тест нумбер уан"',
            'user_name': 'admin',
            'with_results': true,
            'test': {
                'id': '1'
            },
            'users': [
                {
                    'index': '1',
                    'name': 'admin',
                    'birthday': '01.01.2007',
                    'sex': 'Ж',
                    'group': 'Админ',
                    'login': 'admin',
                    'result': '50%'
                },
                {
                    'index': '1',
                    'name': 'admin',
                    'birthday': '01.01.2007',
                    'sex': 'Ж',
                    'group': 'Админ',
                    'login': 'admin',
                    'result': '50%'
                },
                {
                    'index': '1',
                    'name': 'admin',
                    'birthday': '01.01.2007',
                    'sex': 'Ж',
                    'group': 'Админ',
                    'login': 'admin',
                    'result': '50%'
                }
            ]
        }
    },
    '/admin/tests/1/users/admin': {
        'template': 'admin_results.html.hbs',
        'data': {
            'page_title': 'Результаты admin к "Тест нумбер уан"',
            'user_name': 'admin',
            'result': '50%',
            'tasks': [
                {
                    'index': '1',
                    'id': '1',
                    'text': 'Текст билета',
                    'is_passed': true,
                    'answers': [
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': true,
                            'is_checked': true
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        }
                    ]
                },
                {
                    'index': '1',
                    'id': '1',
                    'text': 'Текст билета',
                    'answers': [
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': true
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        }
                    ]
                },
                {
                    'index': '1',
                    'id': '1',
                    'text': 'Текст билета',
                    'answers': [
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': true
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        }
                    ]
                }
            ]
        }
    },
    '/admin/tests/new': {
        'template': 'admin_new_test.html.hbs',
        'data': {
            'page_title': 'Новое тестирование',
            'user_name': 'admin'
        }
    },
    '/tests': {
        'template': 'tests.html.hbs',
        'data': {
            'page_title': 'Тестирования',
            'user_name': 'admin',
            'tests': [
                {
                    'id': '1',
                    'is_passed': false,
                    'name': 'Тест нумбер уан'
                },
                {
                    'id': '1',
                    'is_passed': false,
                    'name': 'Тест нумбер уан'
                },
                {
                    'id': '1',
                    'is_passed': true,
                    'name': 'Тест нумбер уан'
                }
            ]
        }
    },
    '/tests/1': {
        'template': 'task.html.hbs',
        'data': {
            'page_title': 'Тест нумбер уан',
            'user_name': 'admin',
            'test': {
                'id': '1',
                'name': 'Тест нумбер уан',
                'task_count': '2'
            },
            'task': {
                'id': '1',
                'index': '1',
                'text': 'Текст',
                'answers': [
                    {
                        'id': '1',
                        'text': 'Текст ответа'
                    },
                    {
                        'id': '1',
                        'text': 'Текст ответа'
                    },
                    {
                        'id': '1',
                        'text': 'Текст ответа'
                    },
                    {
                        'id': '1',
                        'text': 'Текст ответа'
                    }
                ]
            }
        }
    },
    '/tests/1/results': {
        'template': 'results.html.hbs',
        'data': {
            'page_title': 'Результаты к "Тест нумбер уан"',
            'user_name': 'admin',
            'result': '50%',
            'tasks': [
                {
                    'index': '1',
                    'id': '1',
                    'text': 'Текст билета',
                    'is_passed': true,
                    'answers': [
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': true,
                            'is_checked': true
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        }
                    ]
                },
                {
                    'index': '1',
                    'id': '1',
                    'text': 'Текст билета',
                    'answers': [
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': true
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        }
                    ]
                },
                {
                    'index': '1',
                    'id': '1',
                    'text': 'Текст билета',
                    'answers': [
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': true
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        },
                        {
                            'id': '1',
                            'text': 'Текст ответа',
                            'is_right': false
                        }
                    ]
                }
            ]
        }
    },
};
var partials = [
    '_layout.html.hbs',
    '_profile.html.hbs'
];


partials.forEach(function (partial) {
    Handlebars.registerPartial(partial.replace(/\.html\.hbs/, ''), readTemplate(partial));
});

var app = express();
app.use(express.static(resourcePath('public')));
for (var route in routes) {
    if (!routes.hasOwnProperty(route)) {
        continue;
    }
    (function (route, routeSettings) {
        app.get(route, function (req, res) {
            var template = Handlebars.compile(readTemplate(routeSettings.template));
            res.send(template(routeSettings.data));
        });
    })(route, routes[route]);
}
app.listen(3000);